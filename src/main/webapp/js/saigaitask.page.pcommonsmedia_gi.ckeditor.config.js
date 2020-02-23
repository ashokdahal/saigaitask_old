/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
		config.format_tags = 'h2';
		config.enterMode = CKEDITOR.ENTER_BR;
	 	config.language = 'ja';
	 	config.width  = '98%';
	 	config.allowedContent = true;
		config.toolbar = [
		                  ['Source']
		                  ,['NumberedList','BulletedList']
		                  ,['Link','Unlink']
		                  ,['Format']
		                  ];


	 // config.uiColor = '#AADC6E';


};
