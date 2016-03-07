
var templates = {};
  templates.events = [
  "<div class='<%= category %>' data-id = '<%= id %>'>",
  "<h1><%= title %></h1>",
  "<p> Hosted By <%= userName %></p>",
  "<p><%= location %></p>",
  "<p><%= date.dayOfWeek %>, <%= date.month %><%= date.dayOfMonth %></p>",
  "<button class='addButton'> Attend </button>",
  "</div>"].join("");

var temples = {};
  temples.hostevents = [
  "<div class='<%= category %>' data-id = '<%= id %>'>",
  "<h1><%= title %></h1>",
  "<p> Hosted By <%= userName %></p>",
  "<p><%= location %></p>",
  "<p><%= date.dayOfWeek %>, <%= date.month %><%= date.dayOfMonth %></p>",
  "<button class='button'> Delete </button>",
  "</div>"].join("");

  // templates.attending = [
  //     "<% if(completed:checked) { %>",
  //     "<div class='<%= category %>'>",
  //     "<h1><%= title %></h1>",
  //     "<p> Hosted By <%= userName %></p>",
  //     "<p><%= location %></p>",
  //     "<p><%= date %></p>",
  //     "<input type='checkbox' class='attending'>",
  //     "</input>",
  //     "</div>",
  //     "<% } %>"
  // ].join("")
