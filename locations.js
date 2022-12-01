$(document).ready(function () {
    loadLocations();
})

function loadLocations() {
    var contentRows = $('contentRows');

    $.ajax({
        type: 'GET',
        url: 'api/location',
        success: function(locationArray) {
        console.log(locationArray);
            $.each(locationArray, function(index, location){
                var name = location.name;
                var description = location.description;
                var address = location.address;
                var locationId = location.id;
                var long = location.longitude;
                var lat = location.latitude;
                var row = '<tr>';
                    row += '<td>' + location.id + '</td>'
                    row += '<td>' + name + '</td>';
                    row += '<td>' + description + '</td>';
                    row += '<td>' + address + '</td>';
                    row += '<td>' + long + '</td>';
                    row += '<td>' + lat + '</td>';
                    row += '<td><button type="button" class="btn btn-danger">Edit</button></td>'
                    row += '</tr>'

                    contentRows.append(row);

            })
        
        },
        error: function() {
            $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.'));
        }
    });


}

//File for use in Ajax lesson
//var locationContainer = document.getElementById("locations-info");
//var table = document.getElementById("locations-table");
//
//    var locationsRequest = new XMLHttpRequest();
//    locationsRequest.open('GET', 'http://localhost:9090/api/location/');
//    locationsRequest.onload = function() {
//        var data = JSON.parse(locationsRequest.responseText);
//        console.log(data);
////        renderTable(data);
//    };
//    locationsRequest.send();


//function renderTable(data) {
//
//    for (i = 0; i < data.length; i++) {
//        var row = `<tr>
//                        <td>${data[i].name}</td>
//                        <td>${data[i].description}</td>
//                        <td>${data[i].address}</td>
//                        <td>${data[i].longitude}</td>
//                        <td>${data[i].latitude}</td>
//                        <td><button id=${data[i].id} class = "editBtn" onClick="editDataById(this.id)>Edit</button> | <button id = ${data[i].id} class = "delBtn">Delete</button></td>
//                    </tr>`
//        console.log(row);
//        table.innerHTML += row;
//    }
//
//}
//
//var editBtn = document.getElementsByClassName("editBtn");
//var delBtn = document.getElementsByClassName("delBtn");
//
//function editDataById(id) {
//    editBtn.addEventListener("click", function(id) {
//        // var editRequest = new XMLHttpRequest();
//        // editRequest.open('GET', 'http://localhost:9090/api/location/4')
//        console.log(id);
//    })
//}







