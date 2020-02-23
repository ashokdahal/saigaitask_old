
REM JavaScriptのAPIドキュメントを生成します.
REM JSDOCDIR変数は https://code.google.com/p/jsdoc-toolkit/ をダウンロードしてパスを設定しておくこと

%~d0
cd %~p0

cd ..
java -jar %JSDOCDIR%\jsrun.jar %JSDOCDIR%\app\run.js ".\lib" -t=%JSDOCDIR%\templates\jsdoc -d="jsdoc" --recurse=5

cd %~p0