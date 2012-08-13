@rem ***************************************************************************
@rem
@rem    Copyright 2012 Pedro Ribeiro
@rem
@rem    Licensed under the Apache License, Version 2.0 (the "License");
@rem    you may not use this file except in compliance with the License.
@rem    You may obtain a copy of the License at
@rem
@rem        http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem    Unless required by applicable law or agreed to in writing, software
@rem    distributed under the License is distributed on an "AS IS" BASIS,
@rem    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem    See the License for the specific language governing permissions and
@rem    limitations under the License.
@rem    
@rem ***************************************************************************
@echo off

set SCRIPT_FOLDER=%~dp0

set MAIN_CLASS=org.hashes.ui.HashesCli

set CP=%SCRIPT_FOLDER%\..\etc
for %%i in (%SCRIPT_FOLDER%\..\lib\*.jar) do call set CP=%%CP%%;%%i

java -cp %CP% %MAIN_CLASS% %*