#!/usr/bin/env bash

cd ..
rm -rf android-starter-project-copy
cp -R android-starter-project android-starter-project-copy
cd android-starter-project-copy
python renamePackage.py com.demo.mobile
grep -IR 'myapp' . | grep gradle
find . -name 'myapp' | grep gradle