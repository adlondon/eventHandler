$(document).ready(function () {
  page.init()
})
var username = "";
var page = {
  url: {
    getLogin: "/login"
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
      username = $('input[name ="username"]').val();
      localStorage.setItem('username', username);
      var loginInfo = page.getLoginInfo();

      page.addLogin(loginInfo);
    });

    $('body').on("click", ".navli", function(event) {
        event.preventDefault();
        var selectedPage = '.' + $(this).attr('rel');
        $(selectedPage).siblings('section').removeClass('active');
        $(selectedPage).addClass('active');
    });

    $('body').on("click", ".createSubmit", function (event) {
      event.preventDefault();
      addEvent();
    });
  },

  addLogin: function (data) {
  console.log('this is data', data);
    $.ajax ({
      method: "POST",
      url: page.url.getLogin,
      data: data,
      success: function (createdLogin) {
        console.log("SUCCESS OF LOGIN", createdLogin);
         if (createdLogin === "login success") {
          $('.login').addClass("inactive");
          $('.mainContainer').removeClass("inactive");
         }
         else {
           $('.loginForm').prepend('<div class="tryAgain">That username already exists. Please try again.</div>')
         }
      }
    });
  },

  addEvent: function (newEvent) {
    $.ajax ({
      url: "",
      method: "POST",
      data: newEvent,
      success: function (data) {
        console.log("EVENT CREATED", data)
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
  }











}
