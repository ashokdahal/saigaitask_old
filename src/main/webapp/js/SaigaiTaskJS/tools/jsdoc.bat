
REM JavaScript��API�h�L�������g�𐶐����܂�.
REM JSDOCDIR�ϐ��� https://code.google.com/p/jsdoc-toolkit/ ���_�E�����[�h���ăp�X��ݒ肵�Ă�������

%~d0
cd %~p0

cd ..
java -jar %JSDOCDIR%\jsrun.jar %JSDOCDIR%\app\run.js ".\lib" -t=%JSDOCDIR%\templates\jsdoc -d="jsdoc" --recurse=5

cd %~p0