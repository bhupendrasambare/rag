ALTER TABLE public.document_info
    ALTER COLUMN chat_model DROP NOT NULL;

ALTER TABLE public.document_info
    ALTER COLUMN mime_type DROP NOT NULL;

ALTER TABLE public.document_info
    ALTER COLUMN storage_path DROP NOT NULL;

ALTER TABLE public.document_info
    ALTER COLUMN checksum DROP NOT NULL;