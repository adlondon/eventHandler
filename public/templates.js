
var templates = {};
  templates.events = [
  "<div class='<%= category %>'>",
  "<h1><%= title %></h1>",
  "<p> Hosted By <%= userName %></p>",
  "<p><%= location %></p>",
  "<p><%= date %></p>",
  "<input name = 'completed' type='checkbox' class='attending'>",
  // "<% if(complete) { %>",
  // "line",
  // "<% } %>",
  "</input>",
  "</div>"].join("");

  templates.attending = [
      "<% if(completed:checked) { %>",
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
      "</div>",
      "<% } %>"
  ].join("")
