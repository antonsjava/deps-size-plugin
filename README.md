# deps-size-plugin

 Maven plugin for calculating artifacts size.

 It is similar to dependency:tree but prints also size of artifact.  Artifact is displayed
 like "org.slf4j:slf4j-api:jar:1.7.36 <41.125, 41.125>" where first number is size of 
 artifact jar and second one is calculated sum of dependences (by default with runtime scope).

 Calculated sum can be wrong as same artifact can be more time counted in dependence tree.
 Also single jar packaging can duplicate size. 

 But it is useful to identify candidates to exclude from class path.

## usage

tree view (scope=runtime, order by brutto size)
```
mvn io.github.antonsjava:deps-size-plugin::tree 
```

list view (scope=runtime, order by brutto size)
```
mvn io.github.antonsjava:deps-size-plugin::list 
```


You can change sort by netto by -Dnetto=true param 


You can set another scope by -Dscope=compile. Possible scopes are  'runtime' (default), 
'compile+runtime', 'test', 'compile', 'runtime+system', 'provided', 'system', 'import'.


or you can define script like 
```

#!/bin/bash

function helpinfo() {
	echo "Prints <netto size, brutto size> of depended artifacts "
	echo "Netto size is size of jar file in local repo. Brutto size "
	echo "is sum of brutto size of dependaces. Real sum can be less" 
	echo "because same artifact can be counted more than one time." 
	echo ""
	echo "Usage:"
	echo "  mvn-size.sh [-h|--help] [-list] [-netto] [-Dscope=<scope>]"
	echo "    -h|--help prints help"
	echo "    -list prints list instead of tree"
	echo "    -netto sort by netto size instead of cumulated"
	echo "    -Dscope=<scope> define scope for resolving artifacts"
	echo "       scope can be 'runtime' (default), 'compile+runtime'"
	echo "       'test', 'compile', 'runtime+system', 'provided'"
	echo "       'system', 'import'"
}

param1=$1
param2=$2

params=
name=tree

while [ "$#" -gt 0 ]; do
  case "$1" in
    -h) helpinfo; exit 1;;
    --help) helpinfo; exit 1;;
    -list) name=list; shift 1;;
    -netto) params="$params -Dnetto=true"; shift 1;;
    *) params="$params $1"; shift 1;;
  esac
done

echo "starting"
echo "mvn io.github.antonsjava:deps-size::$name $params"
mvn io.github.antonsjava:deps-size-plugin::$name $params
```

 
