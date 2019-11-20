# FMI2-VDM-Model

This repository contains a VDM model of the FMI version 2.0 standard, plus supporting tools. The repository contains the following projects:

* **FMI2** - a VDM-SL model of the static semantics of FMI 2.0 modelDescription XML files
* **FMI3** - a VDM-SL model of the static semantics of FMI 3.0 modelDescription XML files
* **FMI2_API** - a VDM-SL model of the dynamic semantics of the FMI 2.0 API
* **FMI2_API_CoSim** - a VDM-SL model to test the dynamic semantics of co-simulation FMUs
* **FMI2_API_ModelEx** - a VDM-SL model to test the dynamic semantics of model-exchange FMUs
* **FMI2_Tests** - a set of test XML files for the static semantics
* **COSIM** - a VDM-SL model of the interaction of a set of FMUs
* **FMI2_to_VDM** - a Java tool to convert FMI 2.0 modeDescription XML files to VDM-SL and check them
* **COSIM_to_VDM** - a Java tool to convert Maestro JSON configurations to VDM-SL.

The FMI2 static semantic model and the FMI2_to_VDM tool are packaged in the release to create a stand-alone tool for verifying FMU files, called VDMCheck. This can be used to check existing FMI version 2 FMU files against the standard.

```
$ VDMCheck.sh
Usage: VDMCheck.sh [-v <VDM outfile>] <FMU or modelDescription.xml file>

$ VDMCheck.sh WaterTank_Control.fmu
No errors found.

$ VDMCheck.sh invalidOutputs2.xml
2.2.7 Causality/variability/initial/start <input>/<continuous>/nil/nil invalid at line 6
2.2.7 ScalarVariables["v1"] invalid at line 6
2.2.1 ScalarVariables invalid
2.2.8 Outputs should be omitted at line 10
Errors found.
$
```
