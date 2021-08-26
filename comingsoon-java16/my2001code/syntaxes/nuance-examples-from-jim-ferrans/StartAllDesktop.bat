if exist %PACKAGE%\inventory.mixt goto runme
set PACKAGE=\voxml-ng\n62package\vmlinterp.us
:runme
start /min "License Manager" %NUANCE%\data\license.bat
start /min "Resource Manager" resource-manager 
start /min "Rec Server" recserver -package %PACKAGE% 
start /min "Compilation Server" compilation-server -package %PACKAGE% 
start /min "Rec Client" recclient 
start /min "TTS Server" lhs-server -port 6666 -config_file %NUANCE%\config.tts 
exit 0
