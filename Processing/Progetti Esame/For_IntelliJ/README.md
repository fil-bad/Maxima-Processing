# Using Processing 3 in IntelliJ IDEA

This repository contains an example [Processing 3](https://processing.org) project in [IntelliJ IDEA](https://www.jetbrains.com/idea/).

The project can be used in two steps:

1. Open the project in IntelliJ IDEA
2. Add the same configuration you see on the screenshot
3. Duplicate the example applet and create as many Processing sketches as you want!

![Configuration screenshot](http://cl.ly/image/1b2Q1J2Z1Q1y/processing-intellij.png)

## Setting JavaFx
1. Add to VM Option:
`--module-path $PATH_TO_FX$ --add-modules javafx.controls,javafx.fxml`
    where "$PATH_TO_FX$" should be set on 
	`Settings/Preferences -> Appearance & Behavior -> Path Variables`  as your directory
    For example "C:\javafx-sdk-11.0.2\lib"
	
2.  Go to:
`Project Structure -> Libraries` 
add the same directory of PATH_TO_FX  as library


# About

This project contains the Processing core libraries and is therefore distibuted under [GPL licence](LICENSE.md).

*Thank you Processing!*
