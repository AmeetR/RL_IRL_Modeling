#!/bin/bash

HOME=$( dirname "$( realpath "$0" )" )

# Fix path if running under cygwin:
if uname | grep -iq cygwin ; then
        HOME=$( cygpath -am "$HOME" )
fi

jar="$HOME/teachingbox-core-1.1.1-SNAPSHOT.jar"


### calls default class specified in JAR file
#java  -jar "$jar" "$@"


### calls specific usecase class from org.hswgt.teachingbox.usecases.*
CLASS=org.hswgt.teachingbox.usecases.neuralFittedQ.ReplayMountainCarNFQ   
#CLASS=org.hswgt.teachingbox.usecases.narmedbandit.NArmedBandit
java  -cp "$jar" ${CLASS}
