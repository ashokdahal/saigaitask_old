
REM JavaScript��API�h�L�������g�𐶐����܂�.
REM JSDOC3DIR�ϐ��� https://github.com/jsdoc3/jsdoc ���_�E�����[�h���ăp�X��ݒ肵�Ă�������

%~d0
cd %~p0

cd ..
%JSDOC3DIR%\jsdoc.cmd ".\lib" -t %JSDOC3DIR%\templates\default -d "jsdoc3" -r

cd %~p0