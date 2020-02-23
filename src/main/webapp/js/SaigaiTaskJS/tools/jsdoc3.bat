
REM JavaScriptのAPIドキュメントを生成します.
REM JSDOC3DIR変数は https://github.com/jsdoc3/jsdoc をダウンロードしてパスを設定しておくこと

%~d0
cd %~p0

cd ..
%JSDOC3DIR%\jsdoc.cmd ".\lib" -t %JSDOC3DIR%\templates\default -d "jsdoc3" -r

cd %~p0