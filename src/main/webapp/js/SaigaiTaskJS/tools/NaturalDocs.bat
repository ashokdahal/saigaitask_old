
REM NaturalDocs�L�@�ŃR�����g�������Ă��Ȃ��̂ł��̃t�@�C���͎g�p���Ȃ�

REM NATURALDOCS_HOME�ϐ��� http://www.naturaldocs.org/ ����_�E�����[�h���ăp�X��ݒ肵�Ă�������

%~d0
cd %~p0

cd ..
mkdir apidoc

perl %NATURALDOCS_HOME%\NaturalDocs -i lib -o HTML apidoc -p apidoc_config -s Default OL

cd %~p0