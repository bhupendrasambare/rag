import { useEffect, useState } from 'react';

import {
  Alert,
  Button,
  Form,
  Modal,
  Pagination,
  Spinner,
  Table,
} from 'react-bootstrap';

import documentService, {
  type DocumentResponse,
} from '../services/document.service';

const PAGE_SIZE = 10;

const DocumentsPage = () => {

  const [documents, setDocuments] =
    useState<DocumentResponse[]>([]);

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(0);

  const [totalElements, setTotalElements] =
    useState(0);

  const [loading, setLoading] =
    useState(true);

  const [uploading, setUploading] =
    useState(false);

  const [deletingId, setDeletingId] =
    useState<string | null>(null);

  const [error, setError] =
    useState('');

  const [success, setSuccess] =
    useState('');

  const [showUploadModal, setShowUploadModal] =
    useState(false);

  const [selectedFile, setSelectedFile] =
    useState<File | null>(null);

  const [fileName, setFileName] =
    useState('');


  /*
   * --------------------------------------------------
   * Fetch documents
   * --------------------------------------------------
   */

  useEffect(() => {

    fetchDocuments();

  }, [page]);


  const fetchDocuments = async () => {

    try {

      setLoading(true);
      setError('');

      const data =
        await documentService.getDocuments({
          page,
          size: PAGE_SIZE,
          sort: ['updatedAt,desc'],
        });

      setDocuments(data.content);

      setTotalPages(data.totalPages);

      setTotalElements(
        data.totalElements,
      );

    } catch (err: any) {

      setError(
        err?.response?.data?.message ||
        'Unable to fetch documents.',
      );

    } finally {

      setLoading(false);

    }
  };


  /*
   * --------------------------------------------------
   * Upload modal
   * --------------------------------------------------
   */

  const handleOpenUploadModal = () => {

    setSelectedFile(null);

    setFileName('');

    setError('');

    setShowUploadModal(true);
  };


  const handleCloseUploadModal = () => {

    if (uploading) {
      return;
    }

    setShowUploadModal(false);

    setSelectedFile(null);

    setFileName('');
  };


  /*
   * --------------------------------------------------
   * File selection
   * --------------------------------------------------
   */

  const handleFileChange = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {

    const file =
      event.target.files?.[0];

    if (!file) {

      setSelectedFile(null);

      setFileName('');

      return;
    }

    setSelectedFile(file);

    /*
     * Remove extension.
     *
     * example:
     *
     * document.pdf
     *
     * becomes:
     *
     * document
     */

    const originalName =
      file.name;

    const lastDotIndex =
      originalName.lastIndexOf('.');

    const nameWithoutExtension =
      lastDotIndex > 0
        ? originalName.substring(
            0,
            lastDotIndex,
          )
        : originalName;

    setFileName(
      nameWithoutExtension,
    );
  };


  /*
   * --------------------------------------------------
   * Upload
   * --------------------------------------------------
   */

  const handleUpload = async () => {

    if (!selectedFile) {

      setError(
        'Please select a document.',
      );

      return;
    }

    if (!fileName.trim()) {

      setError(
        'Please enter a document name.',
      );

      return;
    }

    try {

      setUploading(true);

      setError('');

      setSuccess('');

      await documentService.uploadDocument(
        selectedFile,
        fileName.trim(),
      );

      setSuccess(
        'Document uploaded successfully.',
      );

      setShowUploadModal(false);

      setSelectedFile(null);

      setFileName('');

      /*
       * Newly uploaded document should
       * appear on the first page.
       */

      setPage(0);

      /*
       * If already on page 0, use
       * explicit fetch because changing
       * page from 0 -> 0 does not trigger
       * useEffect.
       */

      if (page === 0) {

        await fetchDocuments();
      }

    } catch (err: any) {

      setError(
        err?.response?.data?.message ||
        'Unable to upload document.',
      );

    } finally {

      setUploading(false);
    }
  };


  /*
   * --------------------------------------------------
   * Download
   * --------------------------------------------------
   */

  const handleDownload = async (
    document: DocumentResponse,
  ) => {

    try {

      setError('');

      const blob =
        await documentService.downloadDocument(
          document.id,
        );

      const url =
        window.URL.createObjectURL(blob);

      const anchor =
        window.document.createElement('a');

      anchor.href = url;

      anchor.download =
        document.fileName ||
        document.originalFileName ||
        'document.pdf';

      window.document.body.appendChild(
        anchor,
      );

      anchor.click();

      anchor.remove();

      window.URL.revokeObjectURL(url);

    } catch (err: any) {

      setError(
        err?.response?.data?.message ||
        'Unable to download document.',
      );
    }
  };


  /*
   * --------------------------------------------------
   * Delete
   * --------------------------------------------------
   */

  const handleDelete = async (
    id: string,
  ) => {

    const confirmed =
      window.confirm(
        'Are you sure you want to delete this document?',
      );

    if (!confirmed) {
      return;
    }

    try {

      setDeletingId(id);

      setError('');

      setSuccess('');

      await documentService.deleteDocument(
        id,
      );

      setSuccess(
        'Document deleted successfully.',
      );

      /*
       * If this was the last document
       * on the page, go back one page.
       */

      if (
        documents.length === 1 &&
        page > 0
      ) {

        setPage(
          previousPage =>
            previousPage - 1,
        );

      } else {

        await fetchDocuments();
      }

    } catch (err: any) {

      setError(
        err?.response?.data?.message ||
        'Unable to delete document.',
      );

    } finally {

      setDeletingId(null);
    }
  };


  /*
   * --------------------------------------------------
   * Helpers
   * --------------------------------------------------
   */

  const formatFileSize = (
    bytes: number,
  ) => {

    if (!bytes) {
      return '0 B';
    }

    const units = [
      'B',
      'KB',
      'MB',
      'GB',
    ];

    const index =
      Math.floor(
        Math.log(bytes) /
        Math.log(1024),
      );

    return `${(
      bytes /
      Math.pow(1024, index)
    ).toFixed(
      index === 0 ? 0 : 2,
    )} ${units[index]}`;
  };


  const formatDate = (
    date: string,
  ) => {

    return new Date(
      date,
    ).toLocaleString();
  };


  const getStatusClass = (
    status: string,
  ) => {

    switch (status) {

      case 'COMPLETED':
        return 'bg-success';

      case 'PROCESSING':
        return 'bg-warning text-dark';

      case 'UPLOADING':
        return 'bg-info text-dark';

      case 'FAILED':
        return 'bg-danger';

      default:
        return 'bg-secondary';
    }
  };


  /*
   * --------------------------------------------------
   * Pagination
   * --------------------------------------------------
   */

  const renderPagination = () => {

    if (totalPages <= 1) {
      return null;
    }

    return (
      <Pagination className="mb-0">

        <Pagination.First
          disabled={page === 0}
          onClick={() =>
            setPage(0)
          }
        />

        <Pagination.Prev
          disabled={page === 0}
          onClick={() =>
            setPage(
              previousPage =>
                Math.max(
                  previousPage - 1,
                  0,
                ),
            )
          }
        />

        {Array.from(
          {
            length: totalPages,
          },
          (_, index) => index,
        )
          .filter(
            pageNumber =>
              pageNumber === 0 ||
              pageNumber ===
                totalPages - 1 ||
              Math.abs(
                pageNumber - page,
              ) <= 1,
          )
          .map(pageNumber => (

            <Pagination.Item
              key={pageNumber}
              active={
                pageNumber === page
              }
              onClick={() =>
                setPage(pageNumber)
              }
            >
              {pageNumber + 1}
            </Pagination.Item>

          ))}

        <Pagination.Next
          disabled={
            page === totalPages - 1
          }
          onClick={() =>
            setPage(
              previousPage =>
                Math.min(
                  previousPage + 1,
                  totalPages - 1,
                ),
            )
          }
        />

        <Pagination.Last
          disabled={
            page === totalPages - 1
          }
          onClick={() =>
            setPage(totalPages - 1)
          }
        />

      </Pagination>
    );
  };


  /*
   * --------------------------------------------------
   * UI
   * --------------------------------------------------
   */

  return (
    <div className="container-fluid py-4">

      {/* Header */}

      <div className="d-flex justify-content-between align-items-center mb-4">

        <div>

          <h2 className="fw-bold mb-1">
            Documents
          </h2>

          <p className="text-muted mb-0">
            Upload and manage your documents.
          </p>

        </div>

        <Button
          variant="primary"
          onClick={
            handleOpenUploadModal
          }
        >

          <i className="bi bi-cloud-arrow-up me-2" />

          Upload Document

        </Button>

      </div>


      {/* Alerts */}

      {error && (

        <Alert
          variant="danger"
          dismissible
          onClose={() =>
            setError('')
          }
        >
          {error}
        </Alert>

      )}

      {success && (

        <Alert
          variant="success"
          dismissible
          onClose={() =>
            setSuccess('')
          }
        >
          {success}
        </Alert>

      )}


      {/* Document table */}

      <div className="card border-0 shadow-sm">

        <div className="card-body p-0">

          {loading ? (

            <div className="d-flex justify-content-center align-items-center py-5">

              <Spinner animation="border" />

            </div>

          ) : documents.length === 0 ? (

            <div className="text-center py-5">

              <i className="bi bi-file-earmark-x fs-1 text-muted" />

              <h5 className="mt-3">
                No documents found
              </h5>

              <p className="text-muted">
                Upload your first document
                to get started.
              </p>

              <Button
                variant="primary"
                onClick={
                  handleOpenUploadModal
                }
              >

                <i className="bi bi-cloud-arrow-up me-2" />

                Upload Document

              </Button>

            </div>

          ) : (

            <div className="table-responsive">

              <Table
                hover
                className="mb-0 align-middle"
              >

                <thead className="table-light">

                  <tr>

                    <th className="px-4">
                      Document
                    </th>

                    <th>
                      Type
                    </th>

                    <th>
                      Size
                    </th>

                    <th>
                      Status
                    </th>

                    <th>
                      Updated
                    </th>

                    <th className="text-end px-4">
                      Actions
                    </th>

                  </tr>

                </thead>

                <tbody>

                  {documents.map(
                    document => (

                      <tr
                        key={
                          document.id
                        }
                      >

                        <td className="px-4">

                          <div className="d-flex align-items-center">

                            <div
                              className="bg-light rounded p-2 me-3"
                              style={{
                                width: 42,
                                height: 42,
                              }}
                            >

                              <i className="bi bi-file-earmark-pdf fs-5 text-danger" />

                            </div>

                            <div>

                              <div className="fw-semibold">

                                {
                                  document.fileName
                                }

                              </div>

                              <small className="text-muted">

                                {
                                  document.originalFileName
                                }

                              </small>

                            </div>

                          </div>

                        </td>

                        <td>
                          {
                            document.contentType
                          }
                        </td>

                        <td>
                          {formatFileSize(
                            document.fileSize,
                          )}
                        </td>

                        <td>

                          <span
                            className={`badge ${getStatusClass(
                              document.status,
                            )}`}
                          >
                            {
                              document.status
                            }
                          </span>

                        </td>

                        <td>
                          {formatDate(
                            document.updatedAt,
                          )}
                        </td>

                        <td className="text-end px-4">

                          <Button
                            variant="outline-primary"
                            size="sm"
                            className="me-2"
                            onClick={() =>
                              handleDownload(
                                document,
                              )
                            }
                            disabled={
                              document.status !==
                              'COMPLETED'
                            }
                            title="Download"
                          >

                            <i className="bi bi-download" />

                          </Button>

                          <Button
                            variant="outline-danger"
                            size="sm"
                            onClick={() =>
                              handleDelete(
                                document.id,
                              )
                            }
                            disabled={
                              deletingId ===
                              document.id
                            }
                            title="Delete"
                          >

                            {deletingId ===
                            document.id ? (

                              <Spinner
                                animation="border"
                                size="sm"
                              />

                            ) : (

                              <i className="bi bi-trash" />

                            )}

                          </Button>

                        </td>

                      </tr>

                    ),
                  )}

                </tbody>

              </Table>

            </div>

          )}

        </div>


        {/* Pagination */}

        {!loading &&
          documents.length > 0 && (

            <div className="d-flex justify-content-between align-items-center px-4 py-3 border-top">

              <small className="text-muted">

                {totalElements}{' '}

                {totalElements === 1
                  ? 'document'
                  : 'documents'}

              </small>

              {renderPagination()}

            </div>

          )}

      </div>


      {/* Upload Modal */}

      <Modal
        show={showUploadModal}
        onHide={
          handleCloseUploadModal
        }
        centered
      >

        <Modal.Header closeButton>

          <Modal.Title>
            Upload Document
          </Modal.Title>

        </Modal.Header>


        <Modal.Body>

          <Form>

            <Form.Group className="mb-4">

              <Form.Label>
                Document
              </Form.Label>

              <Form.Control
                type="file"
                accept=".pdf,application/pdf"
                onChange={
                  handleFileChange
                }
                disabled={uploading}
              />

              <Form.Text className="text-muted">

                Currently only PDF
                documents are supported.

              </Form.Text>

            </Form.Group>


            <Form.Group>

              <Form.Label>
                Document Name
              </Form.Label>

              <Form.Control
                type="text"
                value={fileName}
                onChange={event =>
                  setFileName(
                    event.target.value,
                  )
                }
                placeholder="Enter document name"
                disabled={
                  !selectedFile ||
                  uploading
                }
              />

              <Form.Text className="text-muted">

                The name is initially
                taken from the uploaded
                file. You can change it
                before uploading.

              </Form.Text>

            </Form.Group>

          </Form>

        </Modal.Body>


        <Modal.Footer>

          <Button
            variant="secondary"
            onClick={
              handleCloseUploadModal
            }
            disabled={uploading}
          >
            Cancel
          </Button>

          <Button
            variant="primary"
            onClick={handleUpload}
            disabled={
              uploading ||
              !selectedFile ||
              !fileName.trim()
            }
          >

            {uploading ? (

              <>
                <Spinner
                  animation="border"
                  size="sm"
                  className="me-2"
                />

                Uploading...

              </>

            ) : (

              <>
                <i className="bi bi-cloud-arrow-up me-2" />

                Upload

              </>

            )}

          </Button>

        </Modal.Footer>

      </Modal>

    </div>
  );
};

export default DocumentsPage;