// const uploadForm = document.getElementById('first_script')
// window.addEventListener('load', (event) => {
//     const value = uploadForm.innerHTML
//     let result = value.replace(/&quot;/g, '"')
//     let commentHtml = '<script>\n'
//     commentHtml += `jmolApplet(400, 'load 1we7.pdb', '1');`
//     commentHtml += '</script>\n'
//     uploadForm.innerHTML = commentHtml;
// })

// const uploadForm = document.getElementById('file-upload-form')
// uploadForm.addEventListener("submit", handleFormSubmission)
//
// const uploadedContainer = document.getElementById('uploadedCont')
//
// async function handleFormSubmission(event) {
//     event.preventDefault()
//
//     const fileElement = document.getElementById('file-upload')
//
//     // check if user had selected a file
//     if (fileElement.files.length === 0) {
//         alert('please choose a file')
//         return
//     }
//
//     let file = fileElement.files[0]
//
//     let formData = new FormData();
//     formData.set('file', file);
//
//     fetch(`http://localhost:8080/upload`, {
//         method: 'POST',
//         headers: {
//             'Accept': '*/*'
//         },
//         body: JSON.stringify({
//             fullName: file.name,
//             date: "2023-04-30"
//         }),
//         onUploadProgress: (progressEvent) => {
//             const percentCompleted = Math.round(
//                 (progressEvent.loaded * 100) / progressEvent.total
//             );
//             console.log(`upload process: ${percentCompleted}%`);
//         }
//     }).then(res => res.json())
//         .then(res => {
//             console.log(res.data)
//             console.log(res.data.url)
//         })
// }
