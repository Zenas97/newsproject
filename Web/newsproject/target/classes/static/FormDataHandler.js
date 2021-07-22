function getFormDataHandler(){
    var dataObject = {
        titolo:document.querySelector("textarea[name=titolo]").value,
        testo:document.querySelector("textarea[name=testo]").value,
        autore:document.querySelector("input[name=autore]").value,
    }

    for(key in dataObject){
        dataObject[key] = dataObject[key].trim();
    }

    var interface = {
        toJson:function(){
            return JSON.stringify(dataObject);
        },
        resetErrors:function(){
            document.getElementById("result").innerHTML = "";
            document.getElementById("testo").innerHTML = "";
            document.getElementById("titolo").innerHTML = "";
            document.getElementById("autore").innerHTML = "";
        }
    }
    return interface;
}