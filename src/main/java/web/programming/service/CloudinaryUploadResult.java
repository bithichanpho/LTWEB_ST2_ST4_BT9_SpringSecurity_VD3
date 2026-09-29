package web.programming.service;

/**
 * Ket qua upload: url hien thi + publicId (dung de xoa sau nay).
 * publicId bat dau bang "local:" nghia la file luu o may (fallback).
 */
public record CloudinaryUploadResult(String url, String publicId) {
}
