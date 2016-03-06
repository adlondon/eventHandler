
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

<<<<<<< HEAD
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
// };
=======
  // attending: [
  //     "<% if(completed) { %>",
  //     "<div class='<%= category %>'>",
  //     "<h1><%= title %></h1>",
  //     "<p> Hosted By <%= userName %></p>",
  //     "<p><%= location %></p>",
  //     "<p><%= date %></p>",
  //     "<input type='checkbox' class='attending'>",
  //     // "<% if(complete) { %>",
  //     // "line",
  //     // "<% } %>",
  //     "</input>",
  //     "</div>"].join(""),
  //     "<% } %>"
  // ]
};
>>>>>>> d55f15405bfc36f2b001f87ad52c7b4be9bc4fcb
