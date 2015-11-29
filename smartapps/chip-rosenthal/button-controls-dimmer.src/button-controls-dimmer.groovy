/**
 *  Button Controls Dimmer
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
    name: "Button Controls Dimmer",
    namespace: "chip-rosenthal",
    author: "Chip Rosenthal",
    description: "Control a dimmer device with a button. Short push adjusts dimmer level. Long push turns off.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    singleInstance: true)



preferences {
    page(name: "mainPage", title: "Defined Buttons", install: true, uninstall: true, submitOnChange: true) {
        section {
            app(
            	name: "buttonControlsDimmerChild",
                appName: "Button Controls Dimmer Child",
                namespace: "chip-rosenthal",
                title: "Create New Button",
                multiple: true)
        }
    }
}


def installed() {
	log.debug("installed: settings = ${settings}")
	initialize()
}

def updated() {
	log.debug("updated: settings = ${settings}")
	unsubscribe()
	initialize()
}

def initialize() {
    // empty
}

def childUninstalled(cld) {
	// empty
}