Filtered files will appear in the "output" folder.
They include: The name of the error, when it first appeared, when it last appeared, and how many times it appeared.
They will share the same name as the .dsevents file that was parsed, with "ROBOT_ERROR_IDENTIFIER" concatenated onto the end of the filename.
Further parsing can be done through the use of the console and the commands available in the "Commands" Enum within LoggerFilter.java.
Files outputted through the use of Commands can be found in "output\commandoutputs".

Examples of what the client can parse through:

Input:
S_LOG ### <1.00> Robot starting to beep loudly ### E_LOG
S_LOG <<< Warning: <1.01> Timing Overrun >>> E_LOG
S_LOG !!! Error: <2.75> Encoder Disconnected !!! E_LOG
S_LOG ||| Sensor Reading: <2.99> Limit Switch Dead ||| E_LOG
S_LOG <<< Warning: <4.01> Timing Overrun >>> E_LOG
S_LOG !!! Error: <5.75> Encoder Disconnected !!! E_LOG
S_LOG ||| Sensor Reading: <5.99> Limit Switch Dead ||| E_LOG
S_LOG ### <6.00> Robot done beeping loudly ### E_LOG

Output:
Robot Malfunction(s):
"Robot done beeping loudly"
Start: 6.00   End: 6.00   Frequency: 1

"Encoder Disconnected"
Start: 2.75   End: 5.75   Frequency: 2

"Robot starting to beep loudly"
Start: 1.00   End: 1.00   Frequency: 1

"Limit Switch Dead"
Start: 2.99   End: 5.99   Frequency: 2

"Timing Overrun"
Start: 1.01   End: 4.01   Frequency: 2

This can then be filtered through the use of commands. To see what each command does, hover over the buttons in the command panel
and a tooltip will show up describing the command.

COMPOUNDING: A very useful tool. If a command can be compounded, then you can string it together with another compoundable command.
How this works:
Execute your initial command
Set COMPOUNDING to true by clicking the button on the cuntrol panel
Execute your second compoundable command and it will only return values that fall within the parameters you set for both commands.
Ex: Logs within t = 5.00 and t = 7.00 that are also Errors.