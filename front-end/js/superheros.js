$(document).ready(function () {
    loadSuperpowers();
    loadSuperheros();
    
    addSuperhero();
    updateSuperhero();
});

function loadSuperpowers() {
    var superpowerEdit = $('#selectSuperpowerEdit');
    var superpowerAdd = $('#selectSuperpowerAdd');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superpower',
        success: function(superpowerArray) {
            $.each(superpowerArray, function(index, superpower){
                var name = superpower.name;
                var superpowerId = superpower.id;
                var row = '<option value="' + superpowerId + '">' + name + '</option>'
                superpowerEdit.append(row);
                superpowerAdd.append(row); 
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

function loadSuperheros() {
    clearSuperheroTable();
    var contentRows = $('#contentRows');
    
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero',
        success: function(superheroArray) {
            $.each(superheroArray, function(index, superhero){
                var name = superhero.name;
                var superheroId = superhero.id;
                var row = '<tr><td><a href="superhero.html?id=' + superheroId +'">' + name + '</a></td>';
                row += '<td><button type="button" class="btn btn-info" onclick="showEditForm(' + superheroId + ')">Edit</button></td>';
                row += '<td><button type="button" class="btn btn-danger" onclick="deleteSuperhero(' + superheroId + ')">Delete</button></td>';
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

function addSuperhero() {
    $('#addButton').click(function (event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#addForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedSuperpower= $('#selectSuperpowerAdd option:selected').val();

        $.ajax({
           type: 'POST',
           url: 'http://localhost:9090/api/superhero',
           data: JSON.stringify({
                name: $('#addName').val(),
                description: $('#addDescription').val(),
                superpowerId: selectedSuperpower
           }),
           headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
           },
           'dataType': 'json',
           success: function() {
               $('#errorMessages').empty();
               $('#addName').val('');
               $('#addDescription').val('');
               $('#selectSuperpowerAdd').val('');
               loadSuperheros();
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

function clearSuperheroTable() {
    $('#contentRows').empty();
}

function showEditForm(superheroId) {
    $('#errorMessages').empty();

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero/' + superheroId,
        success: function(data, status) {
            $('#editName').val(data.name);
            $('#editDescription').val(data.description);
            $('#editSuperpower').val(data.superpower.id);
            $('#editSuperheroId').val(data.id);
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#superherosTable').hide();
    $('#editFormDiv').show();
}

function hideEditForm() {
    $('#errorMessages').empty();
    
    $('#editName').val('');
    $('#editDescription').val('');
    $('#selectSuperpowerEdit').val('');

    $('#superherosTable').show();
    $('#editFormDiv').hide();
}

function updateSuperhero() {
    $('#updateButton').click(function(event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#editForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedSuperpower = $('#selectSuperpowerEdit option:selected').val();

        $.ajax({
            type: 'PUT',
            url: 'http://localhost:9090/api/superhero/' + $('#editSuperheroId').val(),
            data: JSON.stringify({
                name: $('#editName').val(),
                description: $('#editDescription').val(),
                superpowerId: selectedSuperpower
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                hideEditForm();
                loadSuperheros();
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

function deleteSuperhero(superheroId) {
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/superhero/' + superheroId,
        success: function() {
            loadSuperheros();
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