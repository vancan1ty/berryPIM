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




//http://stackoverflow.com/questions/5999118/add-or-update-query-string-parameter
function updateQueryString(key, value, url) {
    if (!url) url = window.location.href;
    var re = new RegExp("([?&])" + key + "=.*?(&|#|$)(.*)", "gi"),
        hash;

    if (re.test(url)) {
        if (typeof value !== 'undefined' && value !== null)
            return url.replace(re, '$1' + key + "=" + value + '$2$3');
        else {
            hash = url.split('#');
            url = hash[0].replace(re, '$1$3').replace(/(&|\?)$/, '');
            if (typeof hash[1] !== 'undefined' && hash[1] !== null)
                url += '#' + hash[1];
            return url;
        }
    }
    else {
        if (typeof value !== 'undefined' && value !== null) {
            var separator = url.indexOf('?') !== -1 ? '&' : '?';
            hash = url.split('#');
            url = hash[0] + separator + key + '=' + value;
            if (typeof hash[1] !== 'undefined' && hash[1] !== null)
                url += '#' + hash[1];
            return url;
        }
        else
            return url;
    }
}
//http://stackoverflow.com/questions/901115/how-can-i-get-query-string-values-in-javascript
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function setSelectedFileFromURL() {
    var file = getParameterByName("file");
    if (!file) {
        file=document.getElementById("fileselector").value;
    }
    document.getElementById("fileselector").value = file;
}

function doFileSelection() {
    var x = document.getElementById("fileselector").value;
    if(!(x === getParameterByName("file"))) {
        window.location.href = updateQueryString("file",x);
    }
}

function setSelectedGraphTypeFromURL() {
    var gtype = getParameterByName("gtype");
    if (!gtype) {
        gtype="pie";
    }
    document.getElementById("graphSelector").value = gtype;
}

function doGraphControlsSelection() {
    var x = document.getElementById("graphSelector").value;
    if(!(x === getParameterByName("gtype"))) {
        document.location = updateQueryString("gtype",x);
    }
    document.getElementById("pie_options").style.display = "none";
    document.getElementById("bar_options").style.display = "none";
    document.getElementById("line_options").style.display = "none";
    document.getElementById("scatter_options").style.display = "none";
    document.getElementById(x+"_options").style.display = "inline";
}
