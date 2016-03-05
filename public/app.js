$(document).ready(function () {
  page.init()
})
var username = "";
var page = {
  url: {
    getLogin: "/login",
    addEvent: "/add-event"
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
    var category = $('input[name="category"]').val();
    var date = $('input[name="date"]').val();
    var location = $('input[name="location"]').val();
    return{
      title: title,
      category: category,
      date: date,
      location: location,
      // userName: localStorage.getItem('userName'),
    }
  },











}
