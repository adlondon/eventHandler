$(document).ready(function () {
  page.init()
})
var username = "";
var page = {
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
      page.addLogin();
    });

    $('body').on("click", ".navli", function(event){
        event.preventDefault();
        var selectedPage = '.' + $(this).attr('rel');
        $(selectedPage).siblings('section').removeClass('active');
        $(selectedPage).addClass('active');
    }  )
  },

  addLogin: function () {
    $.ajax ({
      url: "http://tiny-tiny.herokuapp.com/collections/eventHandler-getAllEvents",
      method: "POST",
      success: function (successString) {
        console.log("SUCCESS OF LOGIN", successString);
        // if (successString === "login success") {
          $('.login').addClass("inactive");
          $('.mainContainer').removeClass("inactive");
        // }
        // else {
        //   $('.loginForm').prepend('<div class="tryAgain">That username already exists. Please try again.</div>')
        // }
      }
    });
  },





}
