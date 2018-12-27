#!/bin/bash

start=`date +%s.%N`

echo Building archive of $(find assets/ -type f | wc -l) assets
echo

cd assets/
zip -r Assets.zip *

echo
echo Asset building complete!
ls -sh Assets.zip

cp Assets.zip ../out/artifacts/GameEngine_jar/
mv Assets.zip ../

end=`date +%s.%N`

echo
echo Time: $((end-start)) seconds
