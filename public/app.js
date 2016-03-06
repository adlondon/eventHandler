$(document).ready(function () {
  page.init()
  page.addAllEvents(page.getEvent());
})
var username = "";
var page = {
  url: {
    getLogin: "/login",
    addEvent: "/add-event",
    getAllEvents: "/get-all-events",
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

    $('.events').on('click', function (event) {
      event.preventDefault();
      page.addAttendingEvents(page.getEvent())
    })

    $('.createForm').on("click", ".createSubmit", function (event) {
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
        $(this).closest('.mainContainer').addClass('inactive');
        $(this).closest('.mainContainer').siblings('.login').removeClass('inactive');
    });


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
         $('.loginForm').prepend('<div class="tryAgain">That username already exists. Please try again.</div>')
         }
         else {
           $('.login').addClass("inactive");
           $('.mainContainer').removeClass("inactive");
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
      // complete: false
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

addAttendingEvents: function (arr) {
  $('.eventsAttending').html('');
  _.each(arr, function (el) {
    var attTmpl = _.template(templates.events);
    $('.eventsAttending').prepend(attTmpl(el));
  })
}

}
