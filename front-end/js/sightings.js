$(document).ready(function () {
    loadSuperheros();
    loadLocations();
    loadSightings();
    
    addSighting();
    updateSighting();
});

function loadSuperheros() {
    var superherosEdit = $('#selectSuperheroEdit');
    var superherosAdd = $('#selectSuperheroAdd');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero',
        success: function(superheroArray) {
            $.each(superheroArray, function(index, superhero){
                var name = superhero.name;
                var superheroId = superhero.id;
                var row = '<option value="' + superheroId + '">' + name + '</option>'
                superherosEdit.append(row);
                superherosAdd.append(row);   
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

function loadLocations() {
    var locationsEdit = $('#selectLocationEdit');
    var locationsAdd = $('#selectLocationAdd');
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/location',
        success: function(locationArray) {
            $.each(locationArray, function(index, location){
                var name = location.name;
                var locationId = location.id;
                var row = '<option value="' + locationId + '">' + name + '</option>'
                locationsEdit.append(row);
                locationsAdd.append(row);  
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

function loadSightings() {
    clearSightingsTable();
    var contentRows = $('#contentRows');
    
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/sighting',
        success: function(sightingArray) {
            $.each(sightingArray, function(index, sighting){
                var superheroName = sighting.superhero.name;
                var locationName = sighting.location.name;
                var date = sighting.date;
                var sightingId = sighting.id;
                
                var row = '<tr>';
                row += '<td><a href="sighting.html?id=' + sightingId +'">' + sightingId + '</a></td>';
                row += '<td>' + superheroName + '</td>';
                row += '<td>' + locationName + '</td>';
                row += '<td>' + date + '</td>';
                row += '<td><button type="button" class="btn btn-info" onclick="showEditForm(' + sightingId + ')">Edit</button></td>';
                row += '<td><button type="button" class="btn btn-danger" onclick="deleteSighting(' + sightingId + ')">Delete</button></td>';
                row += '</tr>';
                
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

function addSighting() {
    $('#addButton').click(function (event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#addForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedSuperhero = $('#selectSuperheroAdd option:selected').val();
        var selectedLocation = $('#selectLocationAdd option:selected').val();

        $.ajax({
           type: 'POST',
           url: 'http://localhost:9090/api/sighting',
           data: JSON.stringify({
                date: $('#addDate').val(),
                superheroId: selectedSuperhero,
                locationId: selectedLocation
           }),
           headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
           },
           'dataType': 'json',
           success: function() {
               $('#errorMessages').empty();
               $('#addDate').val('');
               $('#selectSuperheroAdd').val('');
               $('#selectLocationAdd').val('');
               loadSightings();
           },
           error: function () {
               $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Sighting must be unique. Date cannot be in the future.')); 
           }
        })
    });
}

function clearSightingsTable() {
    $('#contentRows').empty();
}

function showEditForm(sightingId) {
    $('#errorMessages').empty();
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/sighting/' + sightingId,
        success: function(data, status) {
            $('#editDate').val(data.date);
            $('#editSuperhero').val(data.superhero.id);
            $('#editLocation').val(data.location.id);
            $('#editSightingId').val(sightingId);
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#sightingsTable').hide();
    $('#editFormDiv').show();
}

function hideEditForm() {
    $('#errorMessages').empty();

    $('#editDate').val('');
    $('#selectSuperheroEdit').val('');
    $('#selectLocationEdit').val('');

    $('#sightingsTable').show();
    $('#editFormDiv').hide();
}

function updateSighting() {
    $('#updateButton').click(function(event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#editForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedSuperhero = $('#selectSuperheroEdit option:selected').val();
        var selectedLocation = $('#selectLocationEdit option:selected').val();
        $.ajax({
            type: 'PUT',
            url: 'http://localhost:9090/api/sighting/' +  $('#editSightingId').val(),
            data: JSON.stringify({
                date: $('#editDate').val(),
                superheroId: selectedSuperhero,
                locationId: selectedLocation,
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                hideEditForm();
                loadSightings();
            },
            'error': function() {
                $('#errorMessages')
                .append($('<li>')
                .attr({class: 'list-group-item list-group-item-danger'})
                .text('Sighting must be unique. Date cannot be in the future.')); 
            }
        })
    })
}    

function deleteSighting(sightingId) {
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/sighting/' + sightingId,
        success: function() {
            loadSightings();
        }
    });
}

function checkAndDisplayValidationErrors(input) {
    $('#errorMessages').empty();
    
    var errorMessages = [];
    
    input.each(function() {
        if (!this.validity.valid) {
            var errorField = $('label[for=' + this.id + ']').text();
            errorMessages.push(errorField + ' ' + this.validationMessage);
        }  
    });
    
    if (errorMessages.length > 0){
        $.each(errorMessages,function(index,message) {
            $('#errorMessages').append($('<li>').attr({class: 'list-group-item list-group-item-danger'}).text(message));
        });
        // return true, indicating that there were errors
        return true;
    } else {
        // return false, indicating that there were no errors
        return false;
    }
}