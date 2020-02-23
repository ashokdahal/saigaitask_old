/**
 * @require jQuery 1.7.2+ <http://jquery.com/>
 */


var UI = window.UI || {};

(function ($){

	UI.SlideToggle = {

		init: function (){
			$('body').on('click', '.ui_toggle', UI.SlideToggle.toggle);
		},

		toggle: function (e){
			var $target = $($(this).attr('href'));
			if($target.is(':visible')) $(this).addClass('close');
			else $(this).removeClass('close');
			$target.slideToggle('fast');
			e.preventDefault();
				}

	};


	UI.NavMenu = {

		timer: {},

		init: function (){
			// ログイン名を表示しているヘッダ
			var selector = '#globalnav li#user_header';
			$(selector).hover(
				function (){
					var $this = $(this);
					$this.addClass('hover');
					$this.find('ul.log_menu').show();
					var arrowSpan = $("span.arrow", $this);
					arrowSpan.removeClass("menu_arrow");
					arrowSpan.addClass("menu_arrowd");
					clearTimeout(UI.NavMenu.timer);
				},
				function (){
					var $this = $(this);
					var arrowSpan = $("span.arrow", $this);
					(function(){
						var _$this = $this
						UI.NavMenu.timer = setTimeout(function (){
							_$this.removeClass('hover');
							_$this.find('ul.log_menu').hide();
							arrowSpan.removeClass("menu_arrowd");
							arrowSpan.addClass("menu_arrow");
						}, 500);
					})();
				}
			);
		}

	};


	//$(function (){
		UI.SlideToggle.init();
		UI.NavMenu.init();
	//});

})(jQuery);