
var templates = {};
  templates.events = [
  "<div class='<%= category %>'>",
  "<h1><%= title %></h1>",
  "<p> Hosted By <%= userName %></p>",
  "<p><%= location %></p>",
  "<p><%= date.dayOfWeek %>, <%= date.month %><%= date.dayOfMonth %></p>",
  "<input name = 'completed' type='checkbox' class='attending'>",
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
      "</input>",
      "</div>",
      "<% } %>"
  ].join("")
