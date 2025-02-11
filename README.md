# FMI-VDM-Model

This repository contains a VDM model of the FMI standard, plus supporting tools. The repository contains the following projects:

For FMI2:

* **fmi2.static-model** - a VDM-SL model of the static semantics of FMI 2.0 modelDescription XML files
* **fmi2.dynamic-model** - a VDM-SL model of the dynamic semantics of the FMI 2.0 API
* **fmi2.cosim-test** - a VDM-SL model to test the dynamic semantics of co-simulation FMUs
* **fmi2.modelex-test** - a VDM-SL model to test the dynamic semantics of model-exchange FMUs
* **fmi2.static-tests** - a set of test XML files for the static semantics
* **fmi2.vdmcheck** - a Java tool to convert FMI 2.0 modeDescription XML files to VDM-SL and check them

For FMI3

* **fmi3.static-model** - a VDM-SL model of the static semantics of FMI 3.0 modelDescription XML files
* **fmi3.dynamic-model** - a VDM-SL model of the dynamic semantics of the FMI 3.0 API
* **fmi3.dynamic-matrix** - a spreadsheet of the mode/state callability of the FMI3 API functions
* **fmi3.rules-model** - a new VDM-SL model of static semantics, suitable for integration with the FMI Standard
* **fmi3.ls-bus-model** - a VDM-SL model of the FMI3 Network Communication Layered Standard
* **fmi3.vdmcheck** - a Java tool to convert FMI 3.0 modeDescription XML files to VDM-SL and check them

General projects:

* **fmi2.cosim** - a VDM-SL model of the interaction of a set of FMUs
* **fmi2.cosim2vdm** - a Java tool to convert Maestro JSON configurations to VDM-SL.
* **xsd2vdm** - a Java tool to convert XSD schemas into VDM-SL schemas
* **fmuReader** - a VDMJ external format reader, to allow FMUs to be read directly by VDM tools.

The FMI\{2,3\} static semantic models and the FMI\{2,3\} VDM conversion tools are packaged in the release to create stand-alone tools for verifying FMU files, called **VDMCheck\{2,3\}**. These can be used to check existing FMI version 2 or 3 FMU files against the standard; a pure Java version of each tool is available, which does not depend on the bash shell (eg. for use on Windows).

## Licensing

The VDMCheck tools are issued under a GPLv3 licence. The VDM models sources themselves are issued under an MIT licence. Licence headers and text are included with the package.

## Installation

To install the VDMCheck package, unzip the distribution ZIP (the top level contains a folder called `vdmcheck-<version>`, which you can rename). Add the top level folder to your PATH so that the script within is available.

## Execution
```
$ VDMCheck2.sh
Usage: VDMCheck2.sh [-h2|-h3 <FMI2/3 Standard base URL>] [-v <VDM outfile>] -x <XML> | <file>.fmu | <file>.xml
$ VDMCheck3.sh 
Usage: VDMCheck3.sh [-h <FMI Standard base URL>] [-v <VDM outfile>] <file>.fmu | <file>.xml

or

C:\> java -jar <path to vdmcheck2 installation>/vdmcheck2.jar 
Usage: java -jar vdmcheck2.jar [-h2|-h3 <FMI2/3 Standard base URL>] [-v <VDM outfile>] -x <XML> | <file>.fmu | <file>.xml
C:\> java -jar <path to vdmcheck3 installation>/vdmcheck3.jar 
Usage: java -jar vdmcheck3.jar [-h <FMI Standard base URL>] [-v <VDM outfile>] <file>.fmu | <file>.xml

```
For normal use, the tool would be invoked with a single FMU file, though it is possible to analyse an extracted modelDescription.xml file.

It is possible to capture the VDM-SL version of the XML file that is produced by using the `-v` option, but this is mainly for working with the formal model itself.

If the FMU or XML has no errors, the tool will report `No errors found` and have an exit code of 0.

Otherwise errors are listed on standard output. They consist of a unique test name followed by an error message, plus a link the FMI Standard that is relevant to the error:

```
> VDMCheck2.sh 2.2.1_invalidOutputs2.xml
validCSModelIdentifier: "Test FMU" not valid C variable name at modelDescription:4
https://github.com/modelica/fmi-standard/releases/download/v2.0.4/FMI-Specification-2.0.4.pdf Section 2.1.1, Page 14
validVariableAttributes: Variable "v1" causality/variability/initial/start <input>/<continuous>/nil/nil invalid at modelDescription:6
https://github.com/modelica/fmi-standard/releases/download/v2.0.4/FMI-Specification-2.0.4.pdf Section 2.2.7, Page 46
validOutputs: Outputs should be omitted at modelDescription:10
https://github.com/modelica/fmi-standard/releases/download/v2.0.4/FMI-Specification-2.0.4.pdf Section 2.2.8, Page 58
Errors found.

> VDMCheck3.sh transpose.fmu
validCSIntermediateUpdate: canReturnEarlyAfterIntermediateUpdate requires providesIntermediateUpdate at modelDescription.xml:22
See: https://fmi-standard.org/docs/3.0/#table-CoSimulation-details
Errors found.
```
