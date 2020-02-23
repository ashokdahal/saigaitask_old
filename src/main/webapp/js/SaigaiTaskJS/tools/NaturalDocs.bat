
REM NaturalDocs記法でコメントを書いていないのでこのファイルは使用しない

REM NATURALDOCS_HOME変数は http://www.naturaldocs.org/ からダウンロードしてパスを設定しておくこと

%~d0
cd %~p0

cd ..
mkdir apidoc

perl %NATURALDOCS_HOME%\NaturalDocs -i lib -o HTML apidoc -p apidoc_config -s Default OL

cd %~p0