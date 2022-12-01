$(document).ready(function () {
    loadSuperheros();
    loadOrganizations();
    
    addOrganization();
    updateOrganization();
});

function loadSuperheros() {
    var membersEdit = $('#selectMembersEdit');
    var membersAdd = $('#selectMembersAdd');

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/superhero',
        success: function(superheroArray) {
            $.each(superheroArray, function(index, superhero){
                var name = superhero.name;
                var superheroId = superhero.id;
                var row = '<option value="' + superheroId + '">' + name + '</option>'
                membersEdit.append(row);
                membersAdd.append(row); 
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

function loadOrganizations() {
    clearOrganizationTable();
    var contentRows = $('#contentRows');
    
    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/organization',
        success: function(organizationArray) {
            $.each(organizationArray, function(index, organization){
                var name = organization.name;
                var organizationId = organization.id;
                var row = '<tr><td><a href="organization.html?id=' + organizationId +'">' + name + '</a></td>';
                row += '<td><button type="button" class="btn btn-info" onclick="showEditForm(' + organizationId + ')">Edit</button></td>';
                row += '<td><button type="button" class="btn btn-danger" onclick="deleteOrganization(' + organizationId + ')">Delete</button></td>';
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

function addOrganization() {
    $('#addButton').click(function (event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#addForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedMembers = [];
        $.each($('#selectMembersAdd option:selected'), function(){
            selectedMembers.push($(this).val());
        });
        $.ajax({
           type: 'POST',
           url: 'http://localhost:9090/api/organization',
           data: JSON.stringify({
                name: $('#addName').val(),
                description: $('#addDescription').val(),
                address: $('#addAddress').val(),
                members: selectedMembers
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
               $('#addAddress').val('');
               $('#selectMembersAdd').val('');
               loadOrganizations();
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

function clearOrganizationTable() {
    $('#contentRows').empty();
}

function showEditForm(organizationId) {
    $('#errorMessages').empty();

    $.ajax({
        type: 'GET',
        url: 'http://localhost:9090/api/organization/' + organizationId,
        success: function(data, status) {
            $('#editName').val(data.name);
            $('#editDescription').val(data.description);
            $('#editAddress').val(data.address);
            $('#editOrganizationId').val(data.id);
            
        },
        error: function() {
            $('#errorMessages')
            .append($('<li>')
            .attr({class: 'list-group-item list-group-item-danger'})
            .text('Error calling web service. Please try again later.')); 
        }
    })
    
    $('#organizationsTable').hide();
    $('#editFormDiv').show();
}

function hideEditForm() {
    $('#errorMessages').empty();
    
    $('#editName').val('');
    $('#editDescription').val('');
    $('#editAddress').val('');
    $('#selectMembersEdit').val('');

    $('#organizationsTable').show();
    $('#editFormDiv').hide();
}

function updateOrganization() {
    $('#updateButton').click(function(event) {
        var haveValidationErrors = checkAndDisplayValidationErrors($('#editForm').find('input'));
        if(haveValidationErrors) {
            return false;
        }
        var selectedMembers = [];
        $.each($('#selectMembersEdit option:selected'), function(){
            selectedMembers.push($(this).val());
        });
        $.ajax({
            type: 'PUT',
            url: 'http://localhost:9090/api/organization/' + $('#editOrganizationId').val(),
            data: JSON.stringify({
                //organizationId: $('#editOrganizationId').val(),
                name: $('#editName').val(),
                description: $('#editDescription').val(),
                address: $('#editAddress').val(),
                members: selectedMembers
            }),
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            'success': function() {
                $('#errorMessage').empty();
                hideEditForm();
                loadOrganizations();
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

function deleteOrganization(organizationId) {
    $.ajax({
        type: 'DELETE',
        url: 'http://localhost:9090/api/organization/' + organizationId,
        success: function() {
            loadOrganizations();
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