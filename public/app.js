$(document).ready(function () {
  page.init()
})
var username = "";
var page = {
  url: {
    getLogin: "/login",
    addEvent: "/add-event",
    getAllEvents: "/get-all-events",
    getHostEvents: "/get-host-events",
    deleteUrl: "/delete"

  },

  init: function () {
    page.styling();
    page.events();
  },

  styling: function () {

  },

  events: function () {
    $('.loginForm').on("submit", function (event) {
      event.preventDefault();
      username = $('input[name ="userName"]').val();
      var loginInfo = page.getLoginInfo();
      page.addLogin(loginInfo);
    });

    $('body').on("click", ".navli", function(event) {
        event.preventDefault();
        var selectedPage = '.' + $(this).attr('rel');
        $(selectedPage).siblings('section').removeClass('active');
        $(selectedPage).addClass('active');
    });

    $('.search').on('click', function (event) {
      event.preventDefault();
      page.addAllEvents(page.getEvent());
    });

    $('.user').on('click', function (event) {
     event.preventDefault();
     page.addHostEvents(page.getAllHostEvent());
   });

    $('.events').on('click', function (event) {
      event.preventDefault();
      page.addAttendingEvents(page.getEvent())
    })

    $('input[name="eventSubmit"]').on("click", function (event) {
      event.preventDefault();
      var eventInfo = page.getEventInfo();
      page.addEvent(eventInfo);
      $('input[name="title"]').val("");
      $('input[name="category"]').val("");
      $('input[name="date"]').val("");
      $('input[name="location"]').val("");



    });

    $('body').on('click', '.logout', function(event){
      event.preventDefault();
        $(this).closest('.mainContainer').removeClass('active');
        $(this).closest('.mainContainer').siblings('.login').addClass('active');
    });

    $('body').on('click', 'button', function(event){
      event.preventDefault();
      var id = $(this).data('id');
      window.glob = id;
      page.delete(id);

    })


  },

  addLogin: function (dataObj) {
  console.log('this is data', dataObj);
    $.ajax ({
      method: "POST",
      url: page.url.getLogin,
      data: dataObj,
      success: function (username) {
        console.log("SUCCESS OF LOGIN", username);
        localStorage.setItem('userName', username)
         if (username === "login fail") {
         $('.loginError').addClass("show");
         }
         else {
           $('.mainContainer').addClass("active");
           $('.login').removeClass("active");
           $('.loginError').removeClass("show")
           page.addAllEvents(page.getEvent());
         }
      }
    });
  },

  addEvent: function (newEventArr) {
    $.ajax ({
      url: page.url.addEvent,
      method: "POST",
      data: newEventArr,
      success: function (eventinfo) {
        console.log("EVENT CREATED", eventinfo)
      },
      error: function (err) {
        console.log("ERROR", err)
      }
    })
  },

  getLoginInfo: function () {
    var userName = $('input[name="userName"]').val();
    var password = $('input[name="password"]').val();
    return {
      userName: userName,
      password: password
    };
  },

  getEventInfo: function(){
    var title = $('input[name="title"]').val();
    var category = $('#catSelector').val();
    window.glob = category;
    var date = $('input[name="date"]').val();
    var location = $('input[name="location"]').val();
    return{
      title: title,
      category: category,
      date: date,
      location: location,
      // id: id
    }
  },



  getEvent: function() {
  $.ajax({
    url: page.url.getAllEvents,
    method: 'GET',
    success: function (events) {
        console.log("RECEIVED Events", events);
        window.glob = events;
        page.addAllEvents(JSON.parse(events));
    },
    error: function (err) {
    }
  });
},

  addAllEvents: function (arr) {
  $('.searchContainer').html('');
  _.each(arr, function (el) {
    var tmpl = _.template(templates.events);
    $(".searchContainer").prepend(tmpl(el));
  });

},

getAllHostEvent: function() {
$.ajax({
  url: page.url.getHostEvents,
  method: 'GET',
  success: function (events) {
      console.log("RECEIVED Events", events);
      window.glob = events;
      page.addHostEvents(JSON.parse(events));
  },
  error: function (err) {
  }
});
},

addHostEvents: function (arr) {
$('.userProfile').html('');
_.each(arr, function (el) {
  var tmpl = _.template(temples.hostevents);
  $(".userProfile").prepend(tmpl(el));
});
},

delete: function(id){
  $.ajax({
      method: 'POST',
      url: page.url.deleteUrl,
      data: {id: id},
      success: function(data) {
        console.log(data);
        page.getAllHostEvent();
      },
      error: function(data) {
        console.log("ERR",data);
      }
    })
},

// addAttendingEvents: function (arr) {
//   $('.eventsAttending').html('');
//   _.each(arr, function (el) {
//     var attTmpl = _.template(templates.events);
//     $('.eventsAttending').prepend(attTmpl(el));
//   })
// },

}
