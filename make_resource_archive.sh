#!/bin/bash

echo Building archive of $(find assets/ -type f | wc -l) assets
echo

cd assets/
zip -r Assets.zip *

echo
echo Asset building complete!
ls -sh Assets.zip

mv Assets.zip ../out/artifacts/GameEngine_jar/
