function getRequestHandler(){
    var data;

    function submitRequestAndHandleResponse(request){
        request.then(response => {
            if(response.status == 400) handleFailure(response.json());
            else handleSuccess(response.headers);
        });
    }

    function handleFailure(data){

        data.then(body => {
            for(key in body) document.getElementById(key).innerText = "*" + body[key];
        });
    }

    function handleSuccess(headers){
        document.getElementById("result").innerHTML = "Articolo inviato con successo";
    }
    
    function buildPostRequest(){
        return {
            method: "POST",
            mode: "cors",
            headers:{
                "Content-Type": "application/json"
            },
            body:data
        };
    }

    var interface = {
        makeRequest:function(){
            var request = fetch("http://localhost:8080/articoli",buildPostRequest());
            submitRequestAndHandleResponse(request);
        },
        setData:function(d){
            data = d;
        }
    }
    return interface;
};