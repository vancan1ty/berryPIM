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

function doSync() {
    saveEditorFun(function (xhr) {
        signalSuccess(xhr);
        window.location.href="?action=sync";
    },false);
}

function saveEditor() {
    saveEditorFun(signalSuccess, true);
}

function submitDataForSave(data,fileName,doGITCommit,successCallback,failureCallback) {
        var xhr = new XMLHttpRequest();
        document.getElementById('loadingStr').innerHTML = "IO.";
        document.getElementById("loadingStr").style.display="inline"

        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4) {
                if(xhr.status == 200) {
                    window.requestsCompleted++;
                    successCallback(xhr);
                } else {
                    failureCallback(xhr);
                }
            }
        };

        xhr.open("POST", "?rest=true&action=save&doGITCommit="+doGITCommit+"&file="+fileName, true);
        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xhr.send("data="+encodeURIComponent(data));
}

function standardError(xhr) {
      document.getElementById('loadingStr').innerHTML = "request failed: " + xhr.status;
      console.log(xhr);
}

function makeSuccessFun(numNeeded,successCallback) {
    var out = function(xhr) {if(window.requestsCompleted==numNeeded){successCallback(xhr);}};
    return out;
}

//http://www.w3schools.com/ajax/tryit.asp?filename=tryajax_post2
function saveEditorFun(successCallback, doGITCommit) {
    var fileName = document.getElementById("fileName").innerHTML;
    window.requestsCompleted = 0;
    var helperInitialized = document.getElementById("helperInitialized").innerHTML;
    var helperNode = document.getElementById("savedQueries");
    var helperItems = helperNode.getElementsByTagName("li");
    var helperLines = ""
    for (var i=0; i < helperItems.length; i++) {
        helperLines = helperLines.concat(helperItems[i].innerHTML,"\n");
    }
    if (helperInitialized == "false" && isBlank(helperLines)) {
         window.requestsCompleted++;
    } else {
       submitDataForSave(helperLines,fileName+".bPIMD",doGITCommit,makeSuccessFun(2,successCallback),standardError);
    }

    var editorContents = document.getElementById("mainEditor").value;
    submitDataForSave(editorContents,fileName,doGITCommit,
        function(xhr) {
           editorHash = hashEditorContents(); //update saved hash.
           makeSuccessFun(2,successCallback)(xhr);
        }
    ,standardError);
}

function signalSuccess(xhr) {
      document.getElementById('loadingStr').innerHTML = "request successful.";
      setTimeout(function () {
       document.getElementById("loadingStr").style.display="none";
       document.getElementById('loadingStr').innerHTML = "IO.";
      }, 3000);
}

