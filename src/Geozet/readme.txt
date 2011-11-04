Deze documentatie is gebaseerd op NaturalDocs versie 1.5.0
Zie voor meer informatie:

http://www.naturaldocs.org/

1. Aanmaken API docs:
mkdir apidoc
~/tools/NaturalDocs/NaturalDocs -ro -i lib -o HTML apidoc/ -p apidoc_config/

2. Aanmaken developer docs:
mkdir doc
~/tools/NaturalDocs/NaturalDocs -ro -i lib -o HTML doc/ -p doc_config/
