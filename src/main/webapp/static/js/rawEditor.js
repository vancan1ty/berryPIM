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

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

//http://www.w3schools.com/ajax/tryit.asp?filename=tryajax_post2
function saveEditor() {
    var fileName = document.getElementById("fileName").innerHTML;
    var requestsCompleted = 0;
    var helperInitialized = document.getElementById("helperInitialized").innerHTML;
    var helperNode = document.getElementById("savedQueries");
    var helperItems = helperNode.getElementsByTagName("li");
    var helperLines = ""
    for (var i=0; i < helperItems.length; i++) {
        helperLines = helperLines.concat(helperItems[i].innerHTML,"\n");
    }
    if (helperInitialized == "false" && isBlank(helperLines)) {
         requestsCompleted++;
    } else {
        var xhttp1 = new XMLHttpRequest();
        document.getElementById('loadingStr').innerHTML = "IO.";
        document.getElementById("loadingStr").style.display="inline"

        xhttp1.onreadystatechange = function() {
            if (xhttp1.readyState == 4) {
                if(xhttp1.status == 200) {
                    requestsCompleted++;
                    doSuccessfulLoad(2,requestsCompleted);
                } else {
                    document.getElementById('loadingStr').innerHTML = "request failed: " + xhttp1.status;
                }
            }
        };
        xhttp1.open("POST", "?rest=true&action=save&file="+fileName+".bPIMD", true);
        xhttp1.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhttp1.send("data="+encodeURIComponent(helperLines));
    }

    var editorContents = document.getElementById("mainEditor").value;
    var xhttp2 = new XMLHttpRequest();
    document.getElementById('loadingStr').innerHTML = "IO.";
    document.getElementById("loadingStr").style.display="inline"

    xhttp2.onreadystatechange = function() {
        if (xhttp2.readyState == 4) {
            if(xhttp2.status == 200) {
                requestsCompleted++;
                editorHash = hashEditorContents(); //update saved hash.
                doSuccessfulLoad(2,requestsCompleted);
            } else {
                document.getElementById('loadingStr').innerHTML = "request failed: " + xhttp2.status;
            }
        }
    };
    xhttp2.open("POST", "?rest=true&action=save&file="+fileName, true);
    xhttp2.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp2.send("data="+encodeURIComponent(editorContents));
}

function doSuccessfulLoad(numReqsToComplete, numCompleted) {
   if(numReqsToComplete === numCompleted) {
      document.getElementById('loadingStr').innerHTML = "request successful.";
      setTimeout(function () {
       document.getElementById("loadingStr").style.display="none";
       document.getElementById('loadingStr').innerHTML = "IO.";
      }, 3000);
   }
}
