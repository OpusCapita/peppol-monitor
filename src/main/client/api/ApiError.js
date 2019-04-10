class ApiError extends Error {

    constructor(message, code) {
        super(message);
        this.code = code;
    }

    static getErrorFromResponse(e) {
        if (e)
            throw new ApiError((e.response && e.response.body && e.response.body.message) || e.body || e.message, e.response.status);

        throw new Error('An unknown error occured.');
    }
}

export default ApiError;
