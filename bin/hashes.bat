@REM
@REM    Copyright 2012 Pedro Ribeiro
@REM
@REM    Licensed under the Apache License, Version 2.0 (the "License");
@REM    you may not use this file except in compliance with the License.
@REM    You may obtain a copy of the License at
@REM
@REM        http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM    Unless required by applicable law or agreed to in writing, software
@REM    distributed under the License is distributed on an "AS IS" BASIS,
@REM    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM    See the License for the specific language governing permissions and
@REM    limitations under the License.
@REM

set SCRIPT_FOLDER=%~dp0

set MAIN_CLASS=org.hashes.ui.HashesCli

set CP=%SCRIPT_FOLDER%\..\etc
for %%i in (%SCRIPT_FOLDER%\..\lib\*.jar) do call set CP=%%CP%%;%%i

java -cp %CP% %MAIN_CLASS% %*