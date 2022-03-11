const search = () => {
  let query = document.querySelector("#search_input").value;
  let s = document.querySelector(".search_result");

  if (query == "") {
    s.style.display = "none";
  } else {
    console.log(query);

    //sending request to server

    let url = `http://localhost:8282/search/${query}`;
    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        let text = `<div class='list-group'>`;
        data.forEach((contact) => {
          text += `<a href='/user/contact/${contact.cid}' class='list-group-item list-group-action'>${contact.name}</a>`;
        });
        text += `</div>`;
        s.innerHTML = text;
        s.style.display = "block";
      });
  }
};
