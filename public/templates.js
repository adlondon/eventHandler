
var templates = {
  events : [
  "<div class='<%= category %>'>",
  "<h1><%= title %></h1>",
  "<p> Hosted By <%= userName %></p>",
  "<p><%= location %></p>",
  "<p><%= date %></p>",
  "<input type='checkbox' class='attending'>",
  // "<% if(complete) { %>",
  // "line",
  // "<% } %>",
  "</input>",
  "</div>"].join(""),

  attending: [
      "<% if(completed) { %>",
      "<div class='<%= category %>'>",
      "<h1><%= title %></h1>",
      "<p> Hosted By <%= userName %></p>",
      "<p><%= location %></p>",
      "<p><%= date %></p>",
      "<input type='checkbox' class='attending'>",
      // "<% if(complete) { %>",
      // "line",
      // "<% } %>",
      "</input>",
      "</div>"].join(""),
      "<% } %>"
  ]
};
