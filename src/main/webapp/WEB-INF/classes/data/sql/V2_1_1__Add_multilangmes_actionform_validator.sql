insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'multilangInfoForm.name', message FROM multilangmes_info WHERE messageid='Language name';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'userInfoForm.name', message FROM multilangmes_info WHERE messageid='Name is required.<!--2-->';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'groupInfoForm.name', message FROM multilangmes_info WHERE messageid='Group name<!--2-->';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'unitInfoForm.name', message FROM multilangmes_info WHERE messageid='Unit name is required.';

insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'facebookForm.facebookContent', message FROM multilangmes_info WHERE messageid='Text';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'facebookForm.emailTitle', message FROM multilangmes_info WHERE messageid='Title of e-mail';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'ecomgwpostForm.emailTitle', message FROM multilangmes_info WHERE messageid='Title of e-mail';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'pcommonsmailForm.pcommonsmailTitle', message FROM multilangmes_info WHERE messageid='The title of emergency e-mail';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'pcommonsmailForm.pcommonsmailContent', message FROM multilangmes_info WHERE messageid='Text';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'pcommonsmailForm.emailTitle', message FROM multilangmes_info WHERE messageid='Title of e-mail';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'pcommonsmediaForm.emailTitle', message FROM multilangmes_info WHERE messageid='Title of e-mail';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'twitterForm.twitterContent', message FROM multilangmes_info WHERE messageid='Text';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'twitterForm.emailTitle', message FROM multilangmes_info WHERE messageid='Title of e-mail';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'excellistForm.excellistContent', message FROM multilangmes_info WHERE messageid='Text';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'initSetupForm.domain', message FROM multilangmes_info WHERE messageid='Domain name';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'initSetupForm.localgovtypeid', message FROM multilangmes_info WHERE messageid='Local gov. type';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'initSetupForm.pref', message FROM multilangmes_info WHERE messageid='Prefecture name';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'initSetupForm.prefcode', message FROM multilangmes_info WHERE messageid='Prefecture code';
insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'initSetupForm.alarminterval', message FROM multilangmes_info WHERE messageid='Interval of alarm confirmation(in seconds)';

insert into multilangmes_info(multilanginfoid, messageid, message) select multilanginfoid, 'uploadclearinghouseDataForm.layerid', message FROM multilangmes_info WHERE messageid='Target layer';
