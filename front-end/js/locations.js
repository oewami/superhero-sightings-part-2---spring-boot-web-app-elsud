$(document).ready(function () {
    loadLocations();
    addLocation();
    updateLocation();
})

function loadLocations() {
    clearLocationTable();
    var contentRows = $('#contentRows');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/location',
        success: function(locationArray) {
        console.log(locationArray);
            $.each(locationArray, function(index, location){
                var locationId = location.id;
                var name = location.name;
                var description = location.description;
                var address = location.address;
                var locationId = location.id;
                var long = location.longitude;
                var lat = location.latitude;
                var row = '<tr>';
                    row += '<td>' + locationId + '</td>'
                    row += '<td>' + name + '</td>';
                    row += '<td>' + description + '</td>';
                    row += '<td>' + address + '</td>';
                    row += '<td>' + long + '</td>';
                    row += '<td>' + lat + '</td>';
                    row += '<td><button type="button" class="btn btn-info" onclick="showEditForm(' + locationId + ')">Edit</button></td>';                    
                    row += '<td><button type="button" class="btn btn-danger" onclick="deleteLocation(' + locationId + ')">Delete</button></td>';                    row += '</tr>';

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

function clearLocationTable() {
    $('#contentRows').empty();
}

function addLocation() {
    $('#addButton').click(function (event) {
        $.ajax({
           type: 'POST',
           url: 'http://localhost:9090/api/location',
           data: JSON.stringify({
                name: $('#addName').val(),
                description: $('#addDescription').val(),
                address: $('#addAddress').val(),
                longitude: $('#addLongitude').val(),
                latitude: $('#addLatitude').val()
           }),
           headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
           },
           'dataType': 'json',
           success: function() {
               $('#errorMessages').empty();
               $('#addName').val(''),
               $('#addDescription').val(''),
               $('#addAddress').val(''),
               $('#addLongitude').val(''),
               $('#addLatitude').val('')
               loadLocations();
           },
           error: function () {
               $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.'));
           }
        })
    });
}


function showEditForm(locationId) {
    $('#errorMessages').empty();
    
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/location/' + locationId,
        success: function(data, status) {
            $('#editLocationId').val(locationId),
            $('#editName').val(data.name);
            $('#editDescription').val(data.description),
            $('#editAddress').val(data.address),
            $('#editLongitude').val(data.longitude),
            $('#editLatitude').val(data.latitude)
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#addFormDiv').hide();
    $('#editFormDiv').show();
}

function hideEditForm() {
    $('#errorMessages').empty();
    $('#editName').val('');
    $('#editDescription').val(),
    $('#editAddress').val(),
    $('#editLongitude').val(),
    $('#editLatitude').val()

    $('#addFormDiv').show();
    $('#editFormDiv').hide();
}

function hideAddForm() {
    $('#errorMessages').empty();
    $('#addName').val('');
    $('#addDescription').val(),
    $('#addAddress').val(),
    $('#addLongitude').val(),
    $('#addLatitude').val()

    $('#locationTableDiv').show();
    $('#editFormDiv').show();
}

function updateLocation() {
    $('#updateButton').click(function(event) {
        $.ajax({
            type: 'PUT',
            url:  'http://localhost:9090/api/location/' + $('#editLocationId').val(),
            data: JSON.stringify({
                name: $('#editName').val(),
                description: $('#editDescription').val(),
                address: $('#editAddress').val(),
                longitude: $('#editLongitude').val(),
                latitude: $('#editLatitude').val()
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                clearLocationTable();
                loadLocations();
                hideEditForm();

            },
            'error': function() {
                $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Error calling web service. Please try again later.')); 
            }
        })
    })
}

function deleteLocation(locationId) {
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/location/' + locationId,
        success: function() {
            loadLocations();
        }
    });
}
