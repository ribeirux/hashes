#*******************************************************************************
#
#    Copyright 2012 Pedro Ribeiro
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#    
#*******************************************************************************
#!/bin/bash

SCRIPT_FOLDER=$(dirname $0)

MAIN_CLASS="org.hashes.ui.HashesCli"

CP="$SCRIPT_FOLDER/../etc"
for lib in $SCRIPT_FOLDER/../lib/*.jar ; do
	CP="$CP:$lib"
done

if [ -d "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ]; then
	JAVACMD="$JAVA_HOME/bin/java"
else
	JAVACMD=$(which java)
fi

if [ ! -x "$JAVACMD" ]; then
	echo "Error: java not found"
	exit 1
fi

$JAVACMD -cp $CP $MAIN_CLASS $*