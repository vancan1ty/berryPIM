'use strict'

var editorHash;

//http://stackoverflow.com/questions/7616461/generate-a-hash-from-string-in-javascript-jquery
function hashCode(str) {
  var hash = 0, i, chr, len;
  if (str.length === 0) return hash;
  for (i = 0, len = str.length; i < len; i++) {
    chr   = str.charCodeAt(i);
    hash  = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return hash;
};

function hashEditorContents() {
    var editorContents = document.getElementById("mainEditor").value;
    var myCode = hashCode(editorContents);
    return myCode;
}

window.onload = function() {
    editorHash = hashEditorContents();
};

window.onbeforeunload = function(e) {
    var nHash = hashEditorContents();
    var myLink = document.activeElement.href;
    if(nHash != editorHash) {//then user must have modified the editor
        return 'You have edited the current document.  Would you like to save it before leaving?'
    }
};

var fileName = "contacts.xml"
//http://www.w3schools.com/ajax/tryit.asp?filename=tryajax_post2
function saveEditor() {
    var editorContents = document.getElementById("mainEditor").value;
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "?action=save&file="+fileName, true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send("data="+encodeURIComponent(editorContents));
}
