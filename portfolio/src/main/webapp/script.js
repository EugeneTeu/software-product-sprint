// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/*
 * Finds the tab that has been selected and display that tab
 *
 * @params { object } evt: HTML event
 * @params { sectionName } string: section Name
 */
function openSection(evt, sectionName) {
  const tabContents = document.getElementsByClassName("tab-content");
  for (let i = 0; i < tabContents.length; i++) {
    tabContents[i].style.display = "none";
  }

  const tabLinks = document.getElementsByClassName("tab-links");
  for (let i = 0; i < tabLinks.length; i++) {
    tabLinks[i].className = tabLinks[i].className.replace(" active", "");
  }

  if (document.getElementById(sectionName) !== null) {
    document.getElementById(sectionName).style.display = "block";
    evt.currentTarget.className += " active";
  }
}

/*
 * Takes in a string and creates a list element
 *
 * @params { text } text: string
 */
async function fetchAndUpdateWelcomeMessage() {
  const response = await fetch('/welcome_message');
  const message = await response.text();
  const messageContainer = document.getElementById('welcome-message-container');
  if (messageContainer !== null) {
    messageContainer.innerText = message;
  }
}

/** Creates list element from string */
const createListElement = (text) => {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Fetch comments from server then adds it to the dom */
async function fetchAndUpdateComments() {
  const response = await fetch('/comments');
  const comments = await response.json();
  const commentsContainer = document.getElementById('comments-container');
  if (commentsContainer !== null) {
    commentsContainer.innerHTML = '';
    if (comments && comments.length !== 0) {
      comments.map((comment, index) => commentsContainer.appendChild(createListElement(`comment ${index + 1}: ${comment}`)));
    }
  }
}
