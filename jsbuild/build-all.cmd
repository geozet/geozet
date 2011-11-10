:: build Geozet
pushd geozet\build
jsbuild all.cfg
move Geozet.js ..\..\full\lib\
popd
:: build OpenLayers
cp openlayers\geozet.cfg openlayers\ol_29\build\
pushd openlayers\ol_29\build
.\build.py geozet.cfg
del geozet.cfg
move OpenLayers.js ..\..\..\full\lib\
popd
:: build Ext JS
pushd ext\build
jsbuild geozet.cfg
move ext.js ..\..\full\lib\
popd
:: build GeoExt
cp geoext\geozet.cfg geoext\geoext_07\build\
pushd geoext\geoext_07\build
jsbuild geozet.cfg
del geozet.cfg
move GeoExt.js ..\..\..\full\lib\
popd
:: build full Geozet.js
pushd full\build
jsbuild all.cfg
move Geozet.js ..\..\..\geozet-webapp\src\main\webapp\static\js\
popd
:: init.js
pushd misc\init\build\
jsbuild init.cfg
move init.js ..\..\..\..\geozet-webapp\src\main\webapp\static\js\
popd
:: coreonly.js
pushd misc\coreonly\build\
jsbuild coreonly.cfg
move coreonly.js ..\..\..\..\geozet-webapp\src\main\webapp\static\js\
popd
:: settings.js
pushd misc\settings\build\
:: settings needs to be uncompressed for readability
jsbuild -u settings.cfg
move settings.js ..\..\..\..\geozet-webapp\src\main\webapp\static\js\
popd
