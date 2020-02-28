# BEST PRACTICES TO MAKE THE LOGGER HAPPY: #
###### Treat errors as severe issues, warnings as mid-level statuses, and messages as essentially ignorable when choosing PrintStyles in TelemetryUtil.
#
---
###### When a failure occurs in a specific subsystem, include the CASE-SENSITIVE name of the subsystem.  
#
>This should be the EXACT SAME as one of the names within the list of subsystem names that you >input within the "config" file.
>Subsystems for our robot are listed below.
---
###### Tag actuators properly (bounded by @).
#
###### Read the "README" file and edit the "config" file properly before running the program.
#
#### Current actuators (and the subsystem they are in) within the logger are:
###### Drive:
#
    - "Left Master Falcon"
    - "Right Master Falcon"
    - "Left Slave Falcon"
    - "Right Slave Falcon"
###### Hopper:
#
    - "Left Hopper Motor
    - "Right Hopper Motor"
###### Intake:
#
    - "Left Intake Motor"
    - "Right Intake Motor"
    - "Left Deploy Piston"
    - "Right Deploy Piston"
###### Climb:
#
    - "Left Winch Motor"
    - "Right Winch Motor"
    - "Slide Motor"
###### Shooter:
#
    - "Index Motor"
    - "Left Shooter Motor"
    - "Right Shooter Motor"
###### Limelight:
#
    - "Limelight"

[Link to Source Code (you may need to sign in to view it)](https://github.com/SkylineSpartabots/RobotErrorIdentifier "Spartabots Github - RobotErrorIdentifier")