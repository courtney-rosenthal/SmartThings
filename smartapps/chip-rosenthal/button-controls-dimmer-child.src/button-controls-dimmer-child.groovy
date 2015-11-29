/**
 *  Button Controls Dimmer Child
 *
 * By Chip Rosenthal <chip@unicom.com>
 *  
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */
definition(
    name: "Button Controls Dimmer Child",
    namespace: "chip-rosenthal",
    author: "Chip Rosenthal",
    description: "DO NOT INSTALL THIS APP. Install the \"Button Controls Dimmer\" app instead.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    parent: "chip-rosenthal:Button Controls Dimmer")

preferences {
    page(name: "page1", nextPage: "page2", uninstall: true) {
        section("Button") {  
            input "myButton", "capability.button", \
                title: "Button device:", required: true 
            input "myButtonNumber", "enum", \
                title: "Button number (for remote with multiple buttons):", options: ["1","2","3","4"], required: false
        }
        section("Dimmers") {
            input "myDimmers", "capability.switchLevel", \
                title: "Dimmers:", multiple: true
            input "myDimmerMinValue", "number", \
                title: "Min level (0-100):", range: "0..100", required: true, defaultValue: 10
            input "myDimmerMaxValue", "number", \
                title: "Max level (0-100):", range: "0..100", required: true, defaultValue: 100
            input "myDimmerSteps", "number", \
                title: "Number steps (1 - 10):", range: "1..10", required: true, defaultValue: 2
        }
    }
    
    page(name: "page2", install: true, uninstall: true)
}

def page2() {
    def dfltLabel = (myButtonNumber ? "${myButton}, button ${myButtonNumber}" : myButton)
    dynamicPage(name: "page2") {
    	section("") {
        	label title: "Assign a name", required: true, defaultValue: "$dfltLabel"
            mode title: "Set for specific mode(s)", required: false, multiple: true
        }
	}
}

def installed() {
	log.debug "installed: settings = ${settings}"
	initialize()
}

def updated() {
	log.debug "updated: settings = ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(myButton, "button", buttonHandler)
}


def buttonHandler(evt) {
	log.debug "buttonHandler, invoked: button = ${myButton}, buttonNumber = ${myButtonNumber}, dimmers = ${myDimmers}"
    
	if (myDimmers.size() == 0) {
    	log.debug "buttonHandler: no dimmers selected"
        return
    }
   
    def currentButtonAction = evt.value
    def currentButtonNumber = null
    
    if (myButtonNumber) {   
        try {
            def data = evt.jsonData
            currentButtonNumber = evt.jsonData.buttonNumber as String
        } catch (e) {
            log.warn "caught exception getting event data as json: $e"
        }
        if (myButtonNumber != currentButtonNumber) {
        	log.debug "buttonHandler: currentButtonNumber = ${currentButtonNumber}, ignoring button event"
        	return
        }
    }    
    log.debug "buttonHandler: currentButtonAction = ${currentButtonAction}, currentButtonNumber = ${currentButtonNumber}"

    switch (currentButtonAction) {
    
    case "held":
        log.debug "buttonHandler: turning off, myDimmers = ${myDimmers}"
        for (d in myDimmers) {
        	if (d.hasCommand("off")) {
            	d.off()
            } else {
                d.setLevel(0)
            }
        }
        break
        
    case "pushed":
        def newLevel = calculateNewLevel(myDimmers.first()?.currentValue("level") ?: 0)
        log.debug "buttonHandler: setting dimmer level ${newLevel}, myDimmers = ${myDimmers}"
        myDimmers.each {d -> d.setLevel(newLevel)}
        break
        
    default:
    	log.warn "buttonHandler: bad button event value \"${evt.value}\""
        return      
        
    }
    
}


def calculateNewLevel(currentLevel) {
	if (! myDimmerMinValue) {
    	myDimmerMinValue = 10
    }
    if (! myDimmerMaxValue) {
    	myDimmerMaxValue = 100
    }
    if (! myDimmerSteps) {
    	myDimmerSteps = 2
    }
    
    def step = (myDimmerMaxValue - myDimmerMinValue) / myDimmerSteps
    
    def newLevel
    if (currentLevel == 0) {
    	newLevel = myDimmerMinValue
    } else {
        newLevel = currentLevel + step
        if (newLevel > myDimmerMaxValue) {
            newLevel = myDimmerMinValue
        }	
    }
    
    log.debug "calculateNewLevel: currentLevel = ${currentLevel}, step = ${step}, newLevel = ${newLevel}"
    return newLevel
}
