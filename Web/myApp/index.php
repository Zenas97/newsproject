<!DOCTYPE html>
<html>
    <?php
        $servername = "localhost";
        $password = "admin";
        $username = "admin";
        $database = "news_project";
        $connection = new mysqli($servername,$username,$password);
        if($connection->connect_error){
            die("Connection failed: " . $connection->connect_error);
        }
        echo "Connected Successfully<br>";
        if(mysqli_select_db($connection,$database)){
            echo "Database " . $database . " choosed<br>";
        }

        //Preparo lo statement necessario in caso la richiesta è di post


        $statement = $connection->prepare("Insert into articoli (titolo,testo,autore,data) VALUES(?,?,?,?)");

        $parameters = array("titolo"=>"","autore"=>"","testo" =>"","data"=>"");
        $values = array("titolo"=>"","autore"=>"","testo" =>"","data"=>"");

        try{
            if($statement->bind_param("ssss",$values["titolo"],$values["testo"],$values["autore"],$values["data"])){
                echo "bind happened " . "<br>";
            }
        }catch(Exception $e){
            $e->getMessage();
        }
    

        if($_SERVER["REQUEST_METHOD"] == "POST"){
                $dboperation = true;

                foreach($_POST as $key=>$value){
                    $_POST[$key] = stripslashes($value);
                    $_POST[$key] = trim($value);
                }

                foreach($_POST as $key=>$value){
                    $values[$key] = $value;
                    if(empty($_POST[$key])){
                        $parameters[$key] = "* must insert " . $key;
                        $dboperation = false;
                    }else if($key == "testo" && strlen($value) < 200){
                        $parameters[$key] = "* text must be at least 200 characters long";
                    }
                }

                if($dboperation == true){
                    foreach($values as $key=>$value){
                        echo $value . " <br>";
                        echo var_dump($value) . "<br>";
                    }
                    if($statement->execute()){
                        echo "Insert into DB happened";
                    }
                }else{
                    echo "Inserto into DB won't happen";
                }
        }
    ?>
    <head>
        <title>Project app</title>
        <style>
            .form_input{
                margin-top: 10px;
            }

            #submit_button{
                margin-top: 20px;
                width: 100px;
                height: 25px;
            }
        </style>
    </head>
    <body>
        <h1> Hello App!!!</h1>
        <form method="POST" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
            <div class = "form_input"> 
                <div>Titolo Articolo  </div>
                <input type="text" name="titolo" value="<?php  echo $values["titolo"]; ?>"/>
                <span> <?php  echo $parameters["titolo"]; ?> </span>
            </div>
            <div class = "form_input"> 
                <div> Autore </div>
                <input type="text" name="autore" value= "<?php  echo $values["autore"]; ?> "/>
                <span> <?php  echo $parameters["autore"]; ?> </span>
            </div>
            <div class = "form_input"> 
                <div> Testo articolo </div>
                <textarea name="testo" rows="25" cols="85"><?php  echo $values["testo"]; ?> </textarea>
                <span> <?php  echo $parameters["testo"]; ?> </span>
            </div>
            <div class = "form_input">
                <div> Data </div>
                <input type="date" name="data" value="<?php  if($parameters["data"] == "")echo date('Y-m-d'); else echo $values["data"]; ?>"/>
                <span> <?php  echo $parameters["data"]; ?> </span>
            </div>
            
            <div class = "form_input">
                <input type="submit" value="send" id="submit_button"/>
            </div>

        </form>
    </body>
</html>