var createGldp = function (selectors){
    var selYears = new Array();
    var today = new Date();
    for(var y = 1970;y < today.getFullYear()+5;y++){
        selYears.push(y);
    }
    var opts = {
      cssName: 'drigw',
      width: 240,
      height: 210,
      selectableYears: selYears,
      dowNames: [ lang.__('Day'), lang.__('Month'), lang.__('Fire<!--2-->'), lang.__('Water'), lang.__('Tree'), lang.__('Money'), lang.__('Soil') ],
      monthNames: [ lang.__('January'), lang.__('February'), lang.__('March'), lang.__('April'), lang.__('May'), lang.__('June'), lang.__('July'), lang.__('August'), lang.__('September'), lang.__('October'), lang.__('November'), lang.__('December') ],
      onClick: function(target, cell, date, data) {
            target.val(date.getFullYear() + '-' + (date.getMonth()+1) + '-' +date.getDate());
        },
      onShow: function(cal){
            cal.show();
        },
      updateOptions: function(target,options){
            var seldate = target.val()!=''?new Date(target.val().replace(/-/g,"/")):null;
            var fdate = seldate!=null?seldate:new Date();
            options['selectedDate'] = seldate;
            options['firstDate'] = fdate;
        }
    };
    var gldp = $(selectors).glDatePicker(opts);
}
